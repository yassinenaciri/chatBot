import * as dayjs from 'dayjs';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';

export interface ITache {
  id?: string;
  intitule?: string;
  description?: string;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  utilisateur?: IUtilisateur | null;
}

export class Tache implements ITache {
  constructor(
    public id?: string,
    public intitule?: string,
    public description?: string,
    public dateDebut?: dayjs.Dayjs | null,
    public dateFin?: dayjs.Dayjs | null,
    public utilisateur?: IUtilisateur | null
  ) {}
}

export function getTacheIdentifier(tache: ITache): string | undefined {
  return tache.id;
}
