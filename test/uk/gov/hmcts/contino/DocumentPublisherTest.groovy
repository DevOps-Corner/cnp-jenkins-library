package uk.gov.hmcts.contino

import com.microsoft.azure.documentdb.Document
import com.microsoft.azure.documentdb.DocumentClient
import spock.lang.Specification

class DocumentPublisherTest extends Specification {

  private static final String COLLECTION_LINK = "dbs/jenkins/colls/mycollection"
  private static final String DATA            = '{ \"key\": \"value\"}'
  private static final String PRODUCT         = 'product'
  private static final String COMPONENT       = 'component'
  private static final String ENVIRONMENT     = 'environment'

  def documentPublisher
  def documentClient
  def steps

  def setup() {
    steps = Mock(JenkinsStepMock)
    steps.env >> [BRANCH_NAME: "master",
                  BUILD_NUMBER: "6",
                  BUILD_ID: "56",
                  BUILD_DISPLAY_NAME: "build-display-name",
                  JOB_NAME: "job-name",
                  JOB_BASE_NAME: 'job-base-name',
                  BUILD_TAG: 'build-tag',
                  NODE_NAME: 'node-name']

    documentClient = Mock(DocumentClient)
    documentPublisher = new DocumentPublisher(steps, PRODUCT, COMPONENT, ENVIRONMENT)

  }

  def "publish single document"() {
    when:
      documentPublisher.publish(this.documentClient, COLLECTION_LINK, DATA)
    then:
      1 * documentClient.createDocument(COLLECTION_LINK, _ as Document, null, false)
  }

  def "publish all documents"() {
    when:
      documentPublisher.publishAll(this.documentClient, COLLECTION_LINK, 'testResources/files/perf-reports', '**/*.json')
    then:
      2 * documentClient.createDocument(COLLECTION_LINK, _ as Document, null, false)

  }

  def "get files of pattern **/*.json"() {
    when:
      def files = documentPublisher.findFiles('testResources/files/perf-reports', '**/*.json')

    then:
      files.size() == 2
  }

}
