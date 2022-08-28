import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import dayjs from 'dayjs';

export interface IWeight {
  id?: number;
  value?: number;
  date?: dayjs.Dayjs;
  babyProfile?: IBabyProfile | null;
}

export class Weight implements IWeight {
  constructor(public id?: number, public value?: number, public date?: dayjs.Dayjs, public babyProfile?: IBabyProfile | null) {}
}

export function getWeightIdentifier(weight: IWeight): number | undefined {
  return weight.id;
}
