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

describe('BreastFeed e2e test', () => {
  const breastFeedPageUrl = '/breast-feed';
  const breastFeedPageUrlPattern = new RegExp('/breast-feed(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const breastFeedSample = {};

  let breastFeed: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/breast-feeds+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/breast-feeds').as('postEntityRequest');
    cy.intercept('DELETE', '/api/breast-feeds/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (breastFeed) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/breast-feeds/${breastFeed.id}`,
      }).then(() => {
        breastFeed = undefined;
      });
    }
  });

  it('BreastFeeds menu should load BreastFeeds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('breast-feed');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BreastFeed').should('exist');
    cy.url().should('match', breastFeedPageUrlPattern);
  });

  describe('BreastFeed page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(breastFeedPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BreastFeed page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/breast-feed/new$'));
        cy.getEntityCreateUpdateHeading('BreastFeed');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', breastFeedPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/breast-feeds',
          body: breastFeedSample,
        }).then(({ body }) => {
          breastFeed = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/breast-feeds+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/breast-feeds?page=0&size=20>; rel="last",<http://localhost/api/breast-feeds?page=0&size=20>; rel="first"',
              },
              body: [breastFeed],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(breastFeedPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BreastFeed page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('breastFeed');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', breastFeedPageUrlPattern);
      });

      it('edit button click should load edit BreastFeed page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BreastFeed');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', breastFeedPageUrlPattern);
      });

      it('last delete button click should delete instance of BreastFeed', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('breastFeed').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', breastFeedPageUrlPattern);

        breastFeed = undefined;
      });
    });
  });

  describe('new BreastFeed page', () => {
    beforeEach(() => {
      cy.visit(`${breastFeedPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BreastFeed');
    });

    it('should create an instance of BreastFeed', () => {
      cy.get(`[data-cy="start"]`).type('2022-07-29T15:27').should('have.value', '2022-07-29T15:27');

      cy.get(`[data-cy="end"]`).type('2022-07-29T13:11').should('have.value', '2022-07-29T13:11');

      cy.get(`[data-cy="pain"]`).select('DISCOMFORTING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        breastFeed = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', breastFeedPageUrlPattern);
    });
  });
});
