# 非Compose资源读取

从`0.0.1-neupass.15`开始，`resource-core`在common中直接提供读取能力，
不依赖Compose、ViewModel或应用的业务仓库。

## Android初始化

在`Application.onCreate()`调用`KmmResource.initialize(this)`。
入口位于`com.tencent.tmm.kmmresource.KmmResource`，库只保存Application Context。
无参读取接口在未初始化时抛出带初始化提示的异常。
原有显式传Context的接口仍然可用，也不要求全局初始化。

JVM和iOS不需要初始化。OHOS继续使用配套ArkTS模块的`initResourceCompose(context)`。

## Common API

```kotlin
val title = MR.strings.title.getString()
val greeting = MR.strings.greeting.getString(name)
val template = MR.plurals.items.getQuantityString(count)
val message = MR.plurals.items.getQuantityString(count, count)
val resolved = description.resolve()
val argb = MR.colors.primary.getColor()
val text = MR.files.mapping.readText()
val bytes = MR.assets.document.readBytes()
```

- 字符串、复数和`StringDesc.resolve()`遵循`StringDesc.localeType`。
  `Custom`使用BCP 47语言标签，`System`使用平台默认语言。
- `quantity`只用于选择复数形式，不会自动注入格式化参数。
  JVM旧的`getQuantityString(quantity)`曾隐式格式化数量；升级后应改为
  `getQuantityString(quantity, quantity)`。旧JVM的`StringDesc.Plural`和Compose复数入口
  保留自动格式化数量的行为。
- 格式化参数中的`StringDesc`会先解析。格式说明符继续遵循各平台已有格式化器，
  不保证Java Formatter的全部格式在Apple或OHOS可用。
- `getColor()`返回当前平台应用环境下的ARGB `Int`快照，不绑定某个窗口的主题。
- files和assets均支持`readBytes()`和UTF-8的`readText()`。
  读取是同步的，不自动切线程、不缓存文件内容。缺失或不可读取时抛出异常，
  而不是返回空文本。文件I/O应由调用者安排到合适的线程。
- 图片和字体继续使用平台原生接口或Compose转换；需要原始文件字节时将资源声明为
  files或assets，不把原生drawable或资产目录承诺为原始图片文件。

## 实现边界

- Android的common读取使用Application Context；Compose仍使用当前UI Context。
  自定义语言使用独立的Configuration副本，不修改Application资源配置。
- JVM按完整语言标签、父语言、base逐级回退，并合并依赖JAR的同级资源表。
  `resource-core-jvm`使用ICU4J 77.1选择CLDR复数类别，不再把所有语言简化成英语规则。
  ICU4J仅属于JVM目标，不进入Android或Native依赖。
- iOS保持NSBundle和现有StringDesc解析；原生颜色加载下沉至core，
  Compose保留原来的UI转换和入口。
- OHOS的Kotlin库与本仓库`ohosApp/resource_core`的ArkTS桥必须一同升级。
  新桥的编译SDK要求API 18：自定义语言通过API 12的
  `getOverrideResourceManager`实现，带格式化参数的复数通过API 18的
  `getIntPluralStringByNameSync`实现。使用这些能力的运行设备也必须支持对应API。
  不修改现有Native ResourceManager的全局语言；原始文件仍由Native桥读取。
  字符串与颜色不再使用会跨语言/配置保留旧值的缓存。

## 验证

针对性测试覆盖语言切换、base回退、嵌套描述、复数类别与格式参数分离、
UTF-8文件/assets读取及缺失资源错误。原有跨JAR资源身份测试继续保留。

```shell
./gradlew :resource-core:jvmTest \
  :resource-core:compileDebugKotlinAndroid \
  :resource-compose:compileKotlinJvm \
  :resource-compose:compileDebugKotlinAndroid \
  :resource-generator:compileKotlin
```

发布前还需在支持Kotlin/Native的主机编译三个iOS KLIB并检查Maven目标元数据。
KLIB编译不等于macOS/Xcode最终链接或设备验收。
OHOS目标在缺少KuiklyBase/KNOI工具链时仅能源码审计，不宣称构建或运行通过。
