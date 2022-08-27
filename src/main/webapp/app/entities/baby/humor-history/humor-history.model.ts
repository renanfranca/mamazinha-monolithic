import * as dayjs from 'dayjs';
import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { IHumor } from 'app/entities/baby/humor/humor.model';

export interface IHumorHistory {
  id?: number;
  date?: dayjs.Dayjs | null;
  babyProfile?: IBabyProfile | null;
  humor?: IHumor | null;
}

export class HumorHistory implements IHumorHistory {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs | null,
    public babyProfile?: IBabyProfile | null,
    public humor?: IHumor | null
  ) {}
}

export function getHumorHistoryIdentifier(humorHistory: IHumorHistory): number | undefined {
  return humorHistory.id;
}
