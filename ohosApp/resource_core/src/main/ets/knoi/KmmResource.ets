
export interface OhosResourceService {
  getLocalizedString(resName: string, locale: string, args: Array<string | number>): string;

  getLocalizedPlural(
    resName: string, quantity: number, locale: string, args: Array<string | number>, formatted: boolean
  ): string;

  getString(resName: string): string;

  getString(resName: string,  args: Array<string | number>): string;

  getPlural(resName: string, args: number): string;

  getImage(resName: string): ArrayBuffer;

  getColor(resName: string): number;

  getImageBase64(resName: string): string;

  getFile(resName: string): Uint8Array;

}
