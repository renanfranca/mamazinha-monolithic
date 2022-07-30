import dayjs from 'dayjs/esm';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { IHumor } from 'app/entities/humor/humor.model';
import { Place } from 'app/entities/enumerations/place.model';

export interface INap {
  id?: number;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  place?: Place | null;
  babyProfile?: IBabyProfile | null;
  humor?: IHumor | null;
}

export class Nap implements INap {
  constructor(
    public id?: number,
    public start?: dayjs.Dayjs | null,
    public end?: dayjs.Dayjs | null,
    public place?: Place | null,
    public babyProfile?: IBabyProfile | null,
    public humor?: IHumor | null
  ) {}
}

export function getNapIdentifier(nap: INap): number | undefined {
  return nap.id;
}
