import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('BabyProfile e2e test', () => {
  const babyProfilePageUrl = '/baby-profile';
  const babyProfilePageUrlPattern = new RegExp('/baby-profile(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const babyProfileSample = { name: 'orchid', birthday: '2022-07-29T03:09:13.999Z', userId: 'Response Generic Metal' };

  let babyProfile: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/baby-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/baby-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/baby-profiles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (babyProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/baby-profiles/${babyProfile.id}`,
      }).then(() => {
        babyProfile = undefined;
      });
    }
  });

  it('BabyProfiles menu should load BabyProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('baby-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BabyProfile').should('exist');
    cy.url().should('match', babyProfilePageUrlPattern);
  });

  describe('BabyProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(babyProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BabyProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/baby-profile/new$'));
        cy.getEntityCreateUpdateHeading('BabyProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', babyProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/baby-profiles',
          body: babyProfileSample,
        }).then(({ body }) => {
          babyProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/baby-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/baby-profiles?page=0&size=20>; rel="last",<http://localhost/api/baby-profiles?page=0&size=20>; rel="first"',
              },
              body: [babyProfile],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(babyProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BabyProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('babyProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', babyProfilePageUrlPattern);
      });

      it('edit button click should load edit BabyProfile page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BabyProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', babyProfilePageUrlPattern);
      });

      it('last delete button click should delete instance of BabyProfile', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('babyProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', babyProfilePageUrlPattern);

        babyProfile = undefined;
      });
    });
  });

  describe('new BabyProfile page', () => {
    beforeEach(() => {
      cy.visit(`${babyProfilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BabyProfile');
    });

    it('should create an instance of BabyProfile', () => {
      cy.get(`[data-cy="name"]`).type('Bedfordshire').should('have.value', 'Bedfordshire');

      cy.setFieldImageAsBytesOfEntity('picture', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="birthday"]`).type('2022-07-29T22:13').should('have.value', '2022-07-29T22:13');

      cy.get(`[data-cy="sign"]`).type('Consultant Human mobile').should('have.value', 'Consultant Human mobile');

      cy.get(`[data-cy="main"]`).should('not.be.checked');
      cy.get(`[data-cy="main"]`).click().should('be.checked');

      cy.get(`[data-cy="userId"]`).type('bandwidth User-centric').should('have.value', 'bandwidth User-centric');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        babyProfile = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', babyProfilePageUrlPattern);
    });
  });
});
