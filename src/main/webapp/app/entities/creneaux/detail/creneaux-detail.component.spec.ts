import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CreneauxDetailComponent } from './creneaux-detail.component';

describe('Component Tests', () => {
  describe('Creneaux Management Detail Component', () => {
    let comp: CreneauxDetailComponent;
    let fixture: ComponentFixture<CreneauxDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CreneauxDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ creneaux: { id: 'ABC' } }) },
          },
        ],
      })
        .overrideTemplate(CreneauxDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CreneauxDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load creneaux on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.creneaux).toEqual(expect.objectContaining({ id: 'ABC' }));
      });
    });
  });
});
