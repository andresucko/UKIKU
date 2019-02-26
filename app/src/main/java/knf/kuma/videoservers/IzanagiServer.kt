package knf.kuma.videoservers

import android.content.Context
import knf.kuma.commons.PatternUtil
import knf.kuma.commons.jsoupCookies
import knf.kuma.videoservers.VideoServer.Names.IZANAGI
import org.json.JSONObject

class IzanagiServer(context: Context, baseLink: String) : Server(context, baseLink) {

    override val isValid: Boolean
        get() = baseLink.contains("s=izanagi")

    override val name: String
        get() = IZANAGI

    override val videoServer: VideoServer?
        get() {
            val downLink = PatternUtil.extractLink(baseLink)
            return try {
                var link = JSONObject(jsoupCookies(downLink.replace("embed", "check")).get().body().text()).getString("file").replace("\\", "")
                link = link.replace("/".toRegex(), "//").replace(":////", "://")
                VideoServer(IZANAGI, Option(name, null, link))
            } catch (e: Exception) {
                null
            }

        }
}
