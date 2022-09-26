export interface IHumor {
  id?: number;
  value?: number;
  description?: string;
  emoticoContentType?: string | null;
  emotico?: string | null;
}

export class Humor implements IHumor {
  constructor(
    public id?: number,
    public value?: number,
    public description?: string,
    public emoticoContentType?: string | null,
    public emotico?: string | null
  ) {}
}

export function getHumorIdentifier(humor: IHumor): number | undefined {
  return humor.id;
}
