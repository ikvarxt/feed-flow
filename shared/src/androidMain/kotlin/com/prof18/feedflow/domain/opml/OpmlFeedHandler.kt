package com.prof18.feedflow.domain.opml

import android.util.Xml
import com.prof18.feedflow.attributeValue
import com.prof18.feedflow.contains
import com.prof18.feedflow.domain.model.FeedSource
import com.prof18.feedflow.domain.model.ParsedFeedSource
import com.prof18.feedflow.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.InputStreamReader
import java.io.Reader

internal actual class OpmlFeedHandler(
    private val dispatcherProvider: DispatcherProvider,
) {
    actual suspend fun importFeed(opmlInput: OpmlInput): List<ParsedFeedSource> =
        withContext(dispatcherProvider.default) {
            val inputStream = opmlInput.inputStream
            val feedSources = mutableListOf<ParsedFeedSource>()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false

            val xmlPullParser = factory.newPullParser()
            val reader: Reader = InputStreamReader(inputStream)

            xmlPullParser.setInput(reader)

            var eventType = xmlPullParser.eventType
            var categoryName: String? = null

            // Start parsing the xml
            loop@ while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> when {
                        xmlPullParser.contains(OpmlConstants.OUTLINE) -> {
                            when (xmlPullParser.attributeValue(OpmlConstants.TYPE)) {
                                OpmlConstants.RSS -> {
                                    val builder = ParsedFeedSource.Builder().apply {
                                        title(xmlPullParser.attributeValue(OpmlConstants.TITLE))
                                        url(xmlPullParser.attributeValue(OpmlConstants.XML_URL))
                                        category(categoryName)
                                    }
                                    builder.build()?.let {
                                        feedSources.add(it)
                                    }
                                }

                                null -> {
                                    categoryName = xmlPullParser.attributeValue(OpmlConstants.TITLE)
                                }
                            }
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
            inputStream?.close()
            return@withContext feedSources
        }

    actual suspend fun exportFeed(opmlOutput: OpmlOutput, feedSources: List<FeedSource>): Unit =
        withContext(dispatcherProvider.default) {
            val serializer: XmlSerializer = Xml.newSerializer()

            serializer.setOutput(opmlOutput.outputStream, "UTF-8")
            serializer.startDocument("UTF-8", true)
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

            serializer.startTag(null, "opml")
            serializer.attribute(null, "version", "1.0")

            serializer.startTag(null, "head")
            serializer.startTag(null, "title")
            serializer.text("Subscriptions from FeedFlow")
            serializer.endTag(null, "title")
            serializer.endTag(null, "head")

            serializer.startTag(null, "body")

            for (feedSource in feedSources) {
                serializer.startTag(null, OpmlConstants.OUTLINE)
                serializer.attribute(null, OpmlConstants.TYPE, "rss")
                serializer.attribute(null, "text", feedSource.title)
                serializer.attribute(null, OpmlConstants.TITLE, feedSource.title)
                serializer.attribute(null, OpmlConstants.XML_URL, feedSource.url)
                serializer.endTag(null, "outline")
            }

            serializer.endTag(null, "body")
            serializer.endTag(null, "opml")
            serializer.endDocument()

            serializer.flush()
            opmlOutput.outputStream?.close()
        }
}
