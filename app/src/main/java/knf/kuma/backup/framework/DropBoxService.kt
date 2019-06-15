package knf.kuma.backup.framework

import androidx.preference.PreferenceManager
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import com.google.gson.Gson
import knf.kuma.App
import knf.kuma.backup.Backups
import knf.kuma.backup.objects.BackupObject
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class DropBoxService : BackupService() {

    private var client: DbxClientV2? = null
    private var dbToken: String?
        get() = PreferenceManager.getDefaultSharedPreferences(App.context).getString("db_token", null)
        set(value) = PreferenceManager.getDefaultSharedPreferences(App.context).edit().putString("db_token", value).apply()

    override fun start() {
        if (dbToken != null)
            logIn(dbToken)
    }

    override val isLoggedIn: Boolean
        get() = client != null

    override fun logIn(token: String?): Boolean {
        return if (token != null) {
            dbToken = token
            val requestConfig = DbxRequestConfig.newBuilder("dropbox_app")
                    .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build()
            client = DbxClientV2(requestConfig, token)
            true
        } else {
            Auth.startOAuth2Authentication(App.context, "qtjow4hsk06vt19")
            false
        }
    }

    override fun logOut() {
        client = null
        dbToken = null
    }

    override suspend fun search(id: String): BackupObject<*>? {
        return if (isLoggedIn)
            try {
                val list = client?.files()?.search("", id)?.matches ?: arrayListOf()
                if (list.size > 0) {
                    val downloader = client?.files()?.download("/$id")
                    val reader = InputStreamReader(downloader?.inputStream)
                    val backupObject = Gson().fromJson<Any>(reader.readText().checkResponse(id), Backups.getType(id)) as BackupObject<*>
                    reader.close()
                    downloader?.close()
                    backupObject
                } else
                    null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        else null
    }

    override suspend fun backup(backupObject: BackupObject<*>, id: String): BackupObject<*>? {
        return if (isLoggedIn)
            try {
                client?.files()?.uploadBuilder("/$id")
                        ?.withMute(true)
                        ?.withMode(WriteMode.OVERWRITE)
                        ?.uploadAndFinish(ByteArrayInputStream(Gson().toJson(backupObject, Backups.getType(id)).checkData(id).toByteArray(StandardCharsets.UTF_8)))
                Backups.saveLastBackup()
                backupObject
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        else null
    }
}