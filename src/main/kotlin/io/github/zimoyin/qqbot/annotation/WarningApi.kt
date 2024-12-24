package io.github.zimoyin.qqbot.annotation

/**
 * 该API未经过任何测试，请谨慎使用。
 * 如果你经过了完全测试，那么请提交 issue 告诉我，我会将其删除。
 *
 * 如果你使用了该API，那么请在使用前，进行测试。并加注以下注解在你的API上
 * 1. 在当前的注解的方法下不再进行警告，并且不再向上传播
 * @OptIn(ExperimentalFoundationApi::class)
 * 2. 在当前的注解的类下不再进行警告，并且不再向上传播
 * @OptIn(ExperimentalFoundationApi::class)
 * 3. 在当前的注解的下不再进行警告，并且不再向上传播
 * @file:OptIn(ExperimentalFoundationApi::class)
 * 4. 不再警告并继续向上传播
 * @ExperimentalFoundationApi
 *
 * @author : zimo
 * @date : 2023/12/28
 */
@RequiresOptIn("This API has not undergone any testing, please use it with caution")
annotation class  UntestedApi



/**
 * 未完全稳定的API未来可能会被移除
 * @author : zimo
 * @date : 2023/12/28
 */
@RequiresOptIn("This API has not undergone any testing, please use it with caution")
annotation class  WarningApi
