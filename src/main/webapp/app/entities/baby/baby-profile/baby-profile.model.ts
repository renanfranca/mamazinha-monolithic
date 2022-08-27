import * as dayjs from 'dayjs';

export interface IBabyProfile {
  id?: number;
  name?: string;
  pictureContentType?: string | null;
  picture?: string | null;
  birthday?: dayjs.Dayjs;
  sign?: string | null;
  main?: boolean | null;
  userId?: string | null;
}

export class BabyProfile implements IBabyProfile {
  constructor(
    public id?: number,
    public name?: string,
    public pictureContentType?: string | null,
    public picture?: string | null,
    public birthday?: dayjs.Dayjs,
    public sign?: string | null,
    public main?: boolean | null,
    public userId?: string | null
  ) {
    this.main = this.main ?? false;
  }
}

export function getBabyProfileIdentifier(babyProfile: IBabyProfile): number | undefined {
  return babyProfile.id;
}
