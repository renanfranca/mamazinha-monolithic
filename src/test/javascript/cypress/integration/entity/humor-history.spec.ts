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

describe('HumorHistory e2e test', () => {
  const humorHistoryPageUrl = '/humor-history';
  const humorHistoryPageUrlPattern = new RegExp('/humor-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const humorHistorySample = {};

  let humorHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/humor-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/humor-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/humor-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (humorHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/humor-histories/${humorHistory.id}`,
      }).then(() => {
        humorHistory = undefined;
      });
    }
  });

  it('HumorHistories menu should load HumorHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('humor-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HumorHistory').should('exist');
    cy.url().should('match', humorHistoryPageUrlPattern);
  });

  describe('HumorHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(humorHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HumorHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/humor-history/new$'));
        cy.getEntityCreateUpdateHeading('HumorHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/humor-histories',
          body: humorHistorySample,
        }).then(({ body }) => {
          humorHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/humor-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/humor-histories?page=0&size=20>; rel="last",<http://localhost/api/humor-histories?page=0&size=20>; rel="first"',
              },
              body: [humorHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(humorHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details HumorHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('humorHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorHistoryPageUrlPattern);
      });

      it('edit button click should load edit HumorHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HumorHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of HumorHistory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('humorHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorHistoryPageUrlPattern);

        humorHistory = undefined;
      });
    });
  });

  describe('new HumorHistory page', () => {
    beforeEach(() => {
      cy.visit(`${humorHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('HumorHistory');
    });

    it('should create an instance of HumorHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-07-29T15:03').should('have.value', '2022-07-29T15:03');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        humorHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', humorHistoryPageUrlPattern);
    });
  });
});
