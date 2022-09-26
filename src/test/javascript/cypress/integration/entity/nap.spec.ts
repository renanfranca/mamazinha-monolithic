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

describe('Nap e2e test', () => {
  const napPageUrl = '/nap';
  const napPageUrlPattern = new RegExp('/nap(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const napSample = {};

  let nap: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/naps+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/naps').as('postEntityRequest');
    cy.intercept('DELETE', '/api/naps/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (nap) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/naps/${nap.id}`,
      }).then(() => {
        nap = undefined;
      });
    }
  });

  it('Naps menu should load Naps page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('nap');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Nap').should('exist');
    cy.url().should('match', napPageUrlPattern);
  });

  describe('Nap page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(napPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Nap page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/nap/new$'));
        cy.getEntityCreateUpdateHeading('Nap');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', napPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/naps',
          body: napSample,
        }).then(({ body }) => {
          nap = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/naps+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/naps?page=0&size=20>; rel="last",<http://localhost/api/naps?page=0&size=20>; rel="first"',
              },
              body: [nap],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(napPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Nap page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('nap');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', napPageUrlPattern);
      });

      it('edit button click should load edit Nap page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Nap');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', napPageUrlPattern);
      });

      it('last delete button click should delete instance of Nap', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('nap').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', napPageUrlPattern);

        nap = undefined;
      });
    });
  });

  describe('new Nap page', () => {
    beforeEach(() => {
      cy.visit(`${napPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Nap');
    });

    it('should create an instance of Nap', () => {
      cy.get(`[data-cy="start"]`).type('2022-07-29T04:48').should('have.value', '2022-07-29T04:48');

      cy.get(`[data-cy="end"]`).type('2022-07-29T18:51').should('have.value', '2022-07-29T18:51');

      cy.get(`[data-cy="place"]`).select('BABY_CRIB');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        nap = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', napPageUrlPattern);
    });
  });
});
