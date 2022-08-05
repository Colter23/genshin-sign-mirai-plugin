package top.colter.mirai.plugin.genshin

const val ACT_ID = "e202009291139501"
//const val BH3_ACT_ID = "e202104072769"
const val BH3_ACT_ID = "e202207181446311"
const val APP_VERSION = "2.28.1"

const val USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/${APP_VERSION}"

const val REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id={ACT_ID}&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
const val SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign"

const val ROLE_URL = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz="
const val YS_ROLE_URL = "${ROLE_URL}hk4e_cn"
const val BH3_ROLE_URL = "${ROLE_URL}bh3_cn"

const val AWARD_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=${ACT_ID}"
const val BH3_AWARD_URL = "https://api-takumi.mihoyo.com/event/luna/home?act_id=${BH3_ACT_ID}"

const val INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info?region={region}&act_id=${ACT_ID}&uid={uid}"
const val USER_INFO = "https://bbs-api.mihoyo.com/user/wapi/getUserFullInfo?gids=3"

fun INFO_URL(region: String, uid: String): String {
    return INFO_URL.replace("{region}", region).replace("{uid}", uid)
}

fun REFERER_URL(act: String): String {
    return REFERER_URL.replace("{ACT_ID}", act)
}

const val BH3_INFO_URL = "https://api-takumi.mihoyo.com/event/luna/info?act_id=${BH3_ACT_ID}&region={region}&uid={uid}"
const val BH3_SIGN_URL = "https://api-takumi.mihoyo.com/event/luna/sign"

fun BH3_INFO_URL(region: String, uid: String): String {
    return BH3_INFO_URL.replace("{region}", region).replace("{uid}", uid)
}
