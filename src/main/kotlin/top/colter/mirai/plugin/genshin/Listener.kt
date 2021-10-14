package top.colter.mirai.plugin.genshin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import top.colter.mirai.plugin.genshin.PluginMain.dataFolder
import top.colter.mirai.plugin.genshin.PluginMain.httpUtils
import top.colter.mirai.plugin.genshin.data.*
import top.colter.mirai.plugin.genshin.utils.AesUtils
import java.io.File
import java.net.URL
import java.util.stream.Collectors

@OptIn(ConsoleExperimentalApi::class)
internal object Listener: CoroutineScope by PluginMain.childScope("GenshinListener") {

    private val context: MutableMap<Long, String> = mutableMapOf()
    private val mutex = GenshinTasker.mutex
    private val genshinSub = GenshinTasker.genshinSub
    private var game = "ys"

    /**
     * 两个字：离谱
     */
    fun subscribe() {

        globalEventChannel().subscribeAlways<FriendMessageEvent>{
            val content = message.content

            if (GenshinPluginConfig.mode == 1 && genshinSub.size == 1 && genshinSub[sender.id] == null){
                return@subscribeAlways
            }

            mutex.withLock {

                when(context[sender.id]){
                    "start" -> {
                        if (content == "同意"){
                            sender.sendMessage("请用电脑访问米游社并登陆 https://bbs.mihoyo.com/ys/\n之后按照下图的步骤获取cookie\n并把cookie发送给bot\n(图片发送可能存在一定延迟)")
                            runCatching {
                                dataFolder.resolve("cookie.png").sendAsImageTo(sender)
                            }.onFailure {
                                sender.sendMessage("获取图片失败, 可前往https://github.com/Colter23/genshin-sign-mirai-plugin查看获取cookie步骤")
                            }
                            context[sender.id] = "cookie"
                        }else if (content == "不同意"){
                            sender.sendMessage("有缘再会♥️")
                            context.remove(sender.id)
                        }
                    }

                    "cookie" -> {
                        var cookie: String? = null
                        runCatching {
                            cookie = cookieHandle(content)
                            requireNotNull(cookie).let {
                                httpUtils.cookie = cookie as String

                                var userInfo = UserInfo("","")
                                runCatching info@{
                                    userInfo = httpUtils.getAndDecode<UserInfoData>(USER_INFO).userInfo
                                }
                                val encodeCookie = AesUtils.encrypt(cookie!!, "${sender.id}${userInfo.uid}")?: content
                                if (genshinSub[sender.id] == null){
                                    genshinSub[sender.id] = SubscribeData(true, mutableListOf(AccountData(userInfo.nickname, userInfo.uid, null,null, encodeCookie)))
                                    context[sender.id] = "push"
                                    sender.sendMessage("是否需要推送签到成功结果(如果签到失败一定会推送)\n请回复  需要  或  不需要")
                                }else{
                                    genshinSub[sender.id]?.accounts?.add(AccountData(userInfo.nickname, userInfo.uid, null, null, encodeCookie))
                                    context.remove(sender.id)
                                }

                                sender.sendMessage(gameRole(userInfo.uid,sender.id, game=="ys"))

                            }
                        }.onFailure {
                            sender.sendMessage("cookie不大对呦, 再试试吧\n如果要退出请回复  退出")
                        }
                    }

                    "push" -> {
                        if (content == "需要" || content == "不需要") {
                            if (content == "不需要") {
                                genshinSub[sender.id]?.pushMsg = false
                            }
                            sender.sendMessage("设置完成, 请及时撤回cookie消息\n发送  米哈游签到功能  以查看功能")
                            context.remove(sender.id)
                        }
                    }

                    "delete" -> {
                        if (content == "全部删除"){
                            genshinSub.remove(sender.id)
                            sender.sendMessage("删除成功")
                        }else{
                            genshinSub[sender.id]?.accounts?.removeIf { v -> v.uid == content }
                            genshinSub[sender.id]?.accounts?.forEach { it.gameRoles?.removeIf{v -> v.uid == content} }
                            genshinSub[sender.id]?.accounts?.forEach { it.bh3GameRoles?.removeIf{v -> v.uid == content} }
                            if (genshinSub[sender.id]?.accounts?.size == 0){
                                genshinSub.remove(sender.id)
                            }
                            val msg = accountList(sender.id)
                            if (msg.isEmpty()){
                                sender.sendMessage("列表空")
                            }else{
                                sender.sendMessage(msg)
                            }
                        }
                        context.remove(sender.id)
                    }

                    "bh3","ys" -> {
                        sender.sendMessage(gameRole(content, sender.id, context[sender.id]=="ys"))
                        context.remove(sender.id)
                    }
                }

                when(content){
                    "米哈游签到功能" -> sender.sendMessage("原神签到功能 : 查看功能\n原神签到 : 开启原神签到\n崩坏签到 : 开启崩坏3签到\n添加米游社账号 : 添加米游社账号\n账号列表 : 账号列表\n删除米游社账号 : 删除账号\n临时签到 : 临时执行签到")

                    "原神签到","崩坏签到" -> {
                        if (genshinSub[sender.id] == null){
                            sender.sendMessage("签到功能简介: bot会在每天早7点左右进行签到，每个用户可以设置多个米游社账号，每个米游社账号可以绑定多个原神账号，bot会依次进行签到")
                            sender.sendMessage("用户协议: bot会存储用户的米游社cookie，仅用于签到活动，除此之外不会用于其他活动。\n" +
                                "免责声明: 此功能仅供学习交流，如有异议，请联系bot管理员删除。\n" +
                                "开源地址: https://github.com/Colter23/genshin-sign-mirai-plugin\n\n" +
                                "中途如果不想设置了可以回复  退出  \n" +
                                "如果同意bot存储cookie，请回复  同意  来设置cookie")
                            context[sender.id] = "start"
                            game = if(content=="原神签到") "ys" else "bh3"
                        }else{
                            if (genshinSub[sender.id]?.accounts?.size==1){
                                val account = genshinSub[sender.id]?.accounts?.get(0)!!
                                sender.sendMessage(gameRole(account.uid,sender.id,content=="原神签到"))
                            }else{
                                sender.sendMessage("要开启哪个账号的${content}呢, 请回复‘@’后面的uid")
                                sender.sendMessage(mhyAccountList(sender.id))
                                context[sender.id] = if(content=="原神签到") "ys" else "bh3"
                            }
                        }
                    }

                    "添加米游社账号" -> {
                        if (genshinSub[sender.id] != null){
                            sender.sendMessage("请用电脑访问米游社并登陆 https://bbs.mihoyo.com/ys/\n之后按照下图的步骤获取cookie\n并把cookie发送给bot")
                            runCatching {
                                dataFolder.resolve("cookie.png").sendAsImageTo(sender)
                            }.onFailure {
                                sender.sendMessage("获取图片失败, 可前往https://github.com/Colter23/genshin-sign-mirai-plugin查看获取cookie步骤")
                            }
                            context[sender.id] = "cookie"
                            game = "ys"
                        }else{
                            sender.sendMessage("您还没有开启签到功能哦，先发送  原神签到 或 崩坏签到  开启吧")
                        }
                    }

                    "删除米游社账号" -> {
                        if (genshinSub[sender.id] != null){
                            sender.sendMessage("要删除哪个账号呢, 请回复‘@’后面的uid\n可以回复  全部删除  删除并关闭签到功能")
                            sender.sendMessage(accountList(sender.id))
                            context[sender.id] = "delete"
                        }else{
                            sender.sendMessage("(•_•)")
                        }
                    }

                    "账号列表" -> {
                        if (genshinSub[sender.id] != null){
                            sender.sendMessage(accountList(sender.id))
                        }else{
                            sender.sendMessage("您还没有开启签到功能哦，先发送  原神签到 或 崩坏签到  开启吧")
                        }
                    }

                    "临时签到" -> {
                        if (genshinSub[sender.id] != null){
                            GenshinTasker.signSingle(sender.id)
                            sender.sendMessage("签到完成")
                        }else{
                            sender.sendMessage("您还没有开启签到功能哦，先发送  原神签到 或 崩坏签到  开启吧")
                        }
                    }

                    "退出" -> {
                        if (context[sender.id] != null){
                            context.remove(sender.id)
                        }
                    }
                }
            }
        }
    }

    fun gameRole(uid:String, delegate:Long, isGenshin: Boolean=true): String {
        genshinSub[delegate]?.accounts?.forEach { account ->
            if (account.uid == uid){
                return@gameRole if ((if(isGenshin)account.gameRoles else account.bh3GameRoles)==null){
                    httpUtils.cookie = AesUtils.decrypt(account.cookie, "${delegate}${account.uid}")?:""
                    val roleList = httpUtils.getAndDecode<GameRoles>(if(isGenshin) YS_ROLE_URL else BH3_ROLE_URL).list
                    var name = ""
                    roleList.forEach { r ->
                        name += r.nickname + "、"
                    }
                    name = name.substring(0, name.length-1)
                    if(isGenshin)account.gameRoles=roleList else account.bh3GameRoles=roleList
                    "$name ${if(isGenshin)"旅行者" else "舰长"}, 欢迎使用${if(isGenshin)"原神" else "崩坏"}签到功能"
                }else{
                    "您已开启${if(isGenshin)"原神" else "崩坏"}签到功能"
                }
            }
        }
        return " "
    }

    private fun accountList(id: Long): String{
        runCatching {
            var message = ""
            var a = 1
            val accounts = genshinSub[id]?.accounts
            accounts?.forEach {
                message += "米游社账号$a: ${it.nickname}@${it.uid}\n"
                var r = 1
                it.gameRoles?.forEach { role->
                    message += "--原神账号$r: ${role.nickname}@${role.uid}\n"
                    r++
                }
                r = 1
                it.bh3GameRoles?.forEach { role->
                    message += "--崩坏账号$r: ${role.nickname}@${role.uid}\n"
                    r++
                }
                a++
            }
            message
        }.onSuccess {
            return@accountList it
        }
        return "列表空"
    }

    private fun mhyAccountList(id: Long): String{
        runCatching {
            var message = ""
            var a = 1
            val accounts = genshinSub[id]?.accounts
            accounts?.forEach {
                message += "米游社账号$a: ${it.nickname}@${it.uid}\n"
                a++
            }
            message
        }.onSuccess {
            return@mhyAccountList it
        }
        return "列表空"
    }

    private fun cookieHandle(rawCookie: String): String?{
        val cookieList = rawCookie.split("; ")
        var resultCookie = ""
        var cookieToken = ""
        var accountId = ""
        cookieList.forEach {
            val cookieItem = it.split("=")
            if (cookieItem[0] == "cookie_token"){
                cookieToken = cookieItem[1]
            }else if (cookieItem[0] == "account_id"){
                accountId = cookieItem[1]
            }
        }
        if (cookieToken == "" || accountId == ""){
            return null
        }
        resultCookie = "cookie_token=$cookieToken; account_id=$accountId"
        return resultCookie
    }

    fun stop()  {
        coroutineContext.cancelChildren()
    }
}