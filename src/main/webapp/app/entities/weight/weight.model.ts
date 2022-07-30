import dayjs from 'dayjs/esm';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';

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
