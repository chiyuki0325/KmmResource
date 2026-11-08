import { registerServiceProvider,invoke } from '@kuiklybase/knoi';
import { OhosResourceService } from './KmmResource';
import resourceManager from '@ohos.resourceManager';

export class OhosResourceServiceImpl implements OhosResourceService {
  static context: Context

  private localizedManager(locale: string): resourceManager.ResourceManager {
    const manager = OhosResourceServiceImpl.context.resourceManager
    if (locale.length === 0) return manager
    const configuration = manager.getConfigurationSync()
    configuration.locale = locale
    return manager.getOverrideResourceManager(configuration)
  }

  getLocalizedString(resName: string, locale: string, args: Array<string | number>): string {
    const manager = this.localizedManager(locale)
    return args.length === 0
      ? manager.getStringByNameSync(resName)
      : manager.getStringByNameSync(resName, ...args)
  }

  getLocalizedPlural(
    resName: string, quantity: number, locale: string, args: Array<string | number>, formatted: boolean
  ): string {
    const manager = this.localizedManager(locale)
    return formatted
      ? manager.getIntPluralStringByNameSync(resName, quantity, ...args)
      : manager.getPluralStringByNameSync(resName, quantity)
  }

  getString(resName: string): string;

  getString(resName: string, args: Array<string | number>): string;

  getString(resName: string, args?: Array<string | number>): string {
    if (args != null && args.length > 0) {
      return OhosResourceServiceImpl.context.resourceManager.getStringByNameSync(resName, ...args)
    } else {
      return OhosResourceServiceImpl.context.resourceManager.getStringByNameSync(resName)
    }
  }


  getPlural(resName: string, args: number): string {
    return OhosResourceServiceImpl.context.resourceManager.getPluralStringByNameSync(resName, args)
  }

  getImage(resName: string): Uint8Array {
    return OhosResourceServiceImpl.context.resourceManager.getMediaByNameSync(resName)
  }

  getColor(resName: string): number {
    return OhosResourceServiceImpl.context.resourceManager.getColorByNameSync(resName)
  }

  getImageBase64(resName: string): string {
    return OhosResourceServiceImpl.context.resourceManager.getMediaBase64ByNameSync(resName)
  }

  getFile(resName: string): Uint8Array {
    return OhosResourceServiceImpl.context.resourceManager.getRawFileContentSync(resName)
  }
}

export function initResourceCompose(context: Context) {
  OhosResourceServiceImpl.context = context.getApplicationContext()
  invoke("init_resource_compose",context.resourceManager)
  registerServiceProvider("OhosResourceService", /** 是否单例 **/ true, OhosResourceServiceImpl)
}
