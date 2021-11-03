import * as dayjs from 'dayjs';
import { IEvenement } from 'app/entities/evenement/evenement.model';

export interface IUtilisateur {
  id?: string;
  dateDebut?: dayjs.Dayjs;
  dateFin?: dayjs.Dayjs | null;
  evenements?: IEvenement[] | null;
}

export class Utilisateur implements IUtilisateur {
  constructor(
    public id?: string,
    public dateDebut?: dayjs.Dayjs,
    public dateFin?: dayjs.Dayjs | null,
    public evenements?: IEvenement[] | null
  ) {}
}

export function getUtilisateurIdentifier(utilisateur: IUtilisateur): string | undefined {
  return utilisateur.id;
}
