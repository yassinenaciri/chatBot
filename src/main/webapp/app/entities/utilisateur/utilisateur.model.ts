import { IUser } from 'app/entities/user/user.model';
import { ITache } from 'app/entities/tache/tache.model';

export interface IUtilisateur {
  id?: string;
  nomComplet?: string;
  compte?: IUser | null;
  taches?: ITache[] | null;
}

export class Utilisateur implements IUtilisateur {
  constructor(public id?: string, public nomComplet?: string, public compte?: IUser | null, public taches?: ITache[] | null) {}
}

export function getUtilisateurIdentifier(utilisateur: IUtilisateur): string | undefined {
  return utilisateur.id;
}
