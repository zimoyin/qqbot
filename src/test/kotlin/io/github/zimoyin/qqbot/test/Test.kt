import com.github.zimoyin.qqbot.net.bean.ContactPermission
import com.github.zimoyin.qqbot.net.bean.Permissions


/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


fun main() {
  val permission = ContactPermission(0)
  permission.addPermission(Permissions.PERMISSION_MANAGE_SUB_CHANNEL)
  permission.addPermission(Permissions.PERMISSION_SPEAK_SUB_CHANNEL)
  permission.addPermission(Permissions.PERMISSION_VIEW_SUB_CHANNEL)
  println(permission)
  println(permission.getDifferenceSet())
}
