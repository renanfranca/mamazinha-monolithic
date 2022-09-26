import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import dayjs from 'dayjs';

export interface IHeight {
  id?: number;
  value?: number;
  date?: dayjs.Dayjs;
  babyProfile?: IBabyProfile | null;
}

export class Height implements IHeight {
  constructor(public id?: number, public value?: number, public date?: dayjs.Dayjs, public babyProfile?: IBabyProfile | null) {}
}

export function getHeightIdentifier(height: IHeight): number | undefined {
  return height.id;
}
