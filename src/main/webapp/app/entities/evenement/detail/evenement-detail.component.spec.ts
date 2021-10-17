import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EvenementDetailComponent } from './evenement-detail.component';

describe('Component Tests', () => {
  describe('Evenement Management Detail Component', () => {
    let comp: EvenementDetailComponent;
    let fixture: ComponentFixture<EvenementDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [EvenementDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ evenement: { id: 'ABC' } }) },
          },
        ],
      })
        .overrideTemplate(EvenementDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(EvenementDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load evenement on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.evenement).toEqual(expect.objectContaining({ id: 'ABC' }));
      });
    });
  });
});
