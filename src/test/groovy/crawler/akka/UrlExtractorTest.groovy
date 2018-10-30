package crawler.akka

import akka.actor.ActorRef
import akka.testkit.javadsl.TestKit
import crawler.util.HtmlParser
import spock.lang.Subject

@Subject(UrlExtractor)
class UrlExtractorTest extends ActorBaseTest {

    def parser = Mock(HtmlParser)

    def "test parsing message handling"() {
        given:
        def visitedProbe = new TestKit(system)
        def aggregatorProbe = new TestKit(system)
        def urls = ['url1', 'url2']
        parser.getUrls('url1') >> urls
        parser.getUrls('url2') >> urls

        def urlExtractor = system.actorOf(UrlExtractor.props(visitedProbe.getRef(), aggregatorProbe.getRef(), parser), "testA")
        when:
        urlExtractor.tell(new UrlExtractor.ParseUrl('akka1'), ActorRef.noSender())

        then:
        visitedProbe.expectMsgEquals(new UrlVisited.IsNotVisited('akka1'))
    }
}
