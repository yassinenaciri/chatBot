import { ICreneaux } from 'app/entities/creneaux/creneaux.model';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';

export interface IEvenement {
  id?: string;
  titre?: string;
  description?: string | null;
  localisation?: string | null;
  creneaux?: ICreneaux | null;
  employee?: IUtilisateur | null;
}

export class Evenement implements IEvenement {
  constructor(
    public id?: string,
    public titre?: string,
    public description?: string | null,
    public localisation?: string | null,
    public creneaux?: ICreneaux | null,
    public employee?: IUtilisateur | null
  ) {}
}

export function getEvenementIdentifier(evenement: IEvenement): string | undefined {
  return evenement.id;
}
