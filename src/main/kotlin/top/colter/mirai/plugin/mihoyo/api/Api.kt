package top.colter.mirai.plugin.mihoyo.api

// Salt  Version
const val SALT = "ulInCDohgEs557j0VsPDYnQaaz6KJcv5"
const val APP_VERSION = "2.28.1"

// Game biz
const val YS_BIZ = "hk4e_cn"
const val BH2_BIZ = "bh2_cn"
const val BH3_BIZ = "bh3_cn"
const val WD_BIZ = "nxx_cn"

// Act id
const val YS_ACT_ID = "e202009291139501"
const val BH2_ACT_ID = "e202203291431091"
const val BH3_ACT_ID = "e202207181446311"
const val WD_ACT_ID = "e202202251749321"

// Referer
const val YS_REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=$YS_ACT_ID&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
const val BH2_REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin/bh2/index.html?bbs_auth_required=true&act_id=$BH2_ACT_ID&bbs_presentation_style=fullscreen&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
const val BH3_REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin/bh3/index.html?bbs_auth_required=true&act_id=$BH3_ACT_ID&bbs_presentation_style=fullscreen&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
const val WD_REFERER_URL = "https://webstatic.mihoyo.com/bbs/event/signin/nxx/index.html?bbs_auth_required=true&act_id=$WD_ACT_ID&bbs_presentation_style=fullscreen"

// Award
const val YS_AWARD_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home"
const val AWARD_URL = "https://api-takumi.mihoyo.com/event/luna/home"

// Sign info
const val YS_SIGN_INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info"
const val SIGN_INFO_URL = "https://api-takumi.mihoyo.com/event/luna/info"

// Sign
const val YS_SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign"
const val SIGN_URL = "https://api-takumi.mihoyo.com/event/luna/sign"

// Game role
const val ROLE_URL = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie"

// BBS user
const val USER_INFO = "https://bbs-api.mihoyo.com/user/wapi/getUserFullInfo"

// Agent
const val USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/${APP_VERSION}"
