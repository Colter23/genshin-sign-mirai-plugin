package top.colter.mirai.plugin.genshin

const val ACT_ID = "e202009291139501"
const val APP_VERSION = "2.3.0"

const val USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/${APP_VERSION}"

const val REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=${ACT_ID}&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
const val SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign"

const val ROLE_URL = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn"
const val AWARD_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=${ACT_ID}"
const val INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info?region={region}&act_id=${ACT_ID}&uid={uid}"
const val USER_INFO = "https://bbs-api.mihoyo.com/user/wapi/getUserFullInfo?gids=3"

fun INFO_URL(region: String, uid: String): String {
    return INFO_URL.replace("{region}", region).replace("{uid}", uid)
}
