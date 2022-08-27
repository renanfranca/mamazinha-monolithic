import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { IHumor } from 'app/entities/baby/humor/humor.model';
import { Place } from 'app/entities/enumerations/place.model';
import * as dayjs from 'dayjs';

export interface INap {
  id?: number;
  start?: dayjs.Dayjs;
  end?: dayjs.Dayjs | null;
  place?: Place | null;
  babyProfile?: IBabyProfile;
  humor?: IHumor | null;
}

export class Nap implements INap {
  constructor(
    public id?: number,
    public start?: dayjs.Dayjs,
    public end?: dayjs.Dayjs | null,
    public place?: Place | null,
    public babyProfile?: IBabyProfile,
    public humor?: IHumor | null
  ) {}
}

export function getNapIdentifier(nap: INap): number | undefined {
  return nap.id;
}
