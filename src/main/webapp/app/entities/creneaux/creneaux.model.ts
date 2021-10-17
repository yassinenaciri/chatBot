import * as dayjs from 'dayjs';

export interface ICreneaux {
  id?: string;
  dateDebut?: dayjs.Dayjs;
  dateFin?: dayjs.Dayjs | null;
}

export class Creneaux implements ICreneaux {
  constructor(public id?: string, public dateDebut?: dayjs.Dayjs, public dateFin?: dayjs.Dayjs | null) {}
}

export function getCreneauxIdentifier(creneaux: ICreneaux): string | undefined {
  return creneaux.id;
}
