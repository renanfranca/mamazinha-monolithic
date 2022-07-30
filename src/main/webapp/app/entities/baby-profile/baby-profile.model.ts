import dayjs from 'dayjs/esm';

export interface IBabyProfile {
  id?: number;
  name?: string;
  pictureContentType?: string | null;
  picture?: string | null;
  birthday?: dayjs.Dayjs;
  sign?: string | null;
  main?: boolean | null;
  userId?: string;
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
    public userId?: string
  ) {
    this.main = this.main ?? false;
  }
}

export function getBabyProfileIdentifier(babyProfile: IBabyProfile): number | undefined {
  return babyProfile.id;
}
