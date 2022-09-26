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

describe('Humor e2e test', () => {
  const humorPageUrl = '/humor';
  const humorPageUrlPattern = new RegExp('/humor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const humorSample = { value: 60970, description: 'Station' };

  let humor: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/humors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/humors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/humors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (humor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/humors/${humor.id}`,
      }).then(() => {
        humor = undefined;
      });
    }
  });

  it('Humors menu should load Humors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('humor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Humor').should('exist');
    cy.url().should('match', humorPageUrlPattern);
  });

  describe('Humor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(humorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Humor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/humor/new$'));
        cy.getEntityCreateUpdateHeading('Humor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/humors',
          body: humorSample,
        }).then(({ body }) => {
          humor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/humors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/humors?page=0&size=20>; rel="last",<http://localhost/api/humors?page=0&size=20>; rel="first"',
              },
              body: [humor],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(humorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Humor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('humor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorPageUrlPattern);
      });

      it('edit button click should load edit Humor page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Humor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorPageUrlPattern);
      });

      it('last delete button click should delete instance of Humor', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('humor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', humorPageUrlPattern);

        humor = undefined;
      });
    });
  });

  describe('new Humor page', () => {
    beforeEach(() => {
      cy.visit(`${humorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Humor');
    });

    it('should create an instance of Humor', () => {
      cy.get(`[data-cy="value"]`).type('19641').should('have.value', '19641');

      cy.get(`[data-cy="description"]`).type('Borders Implementation').should('have.value', 'Borders Implementation');

      cy.setFieldImageAsBytesOfEntity('emotico', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        humor = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', humorPageUrlPattern);
    });
  });
});
