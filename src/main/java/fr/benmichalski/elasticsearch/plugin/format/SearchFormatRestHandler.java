package fr.benmichalski.elasticsearch.plugin.format;

import au.com.bytecode.opencsv.CSVWriter;
import java.nio.charset.Charset;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.rest.*;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import org.elasticsearch.rest.action.search.RestSearchAction;

public class SearchFormatRestHandler extends BaseRestHandler {

    @Inject
    public SearchFormatRestHandler(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);

        controller.registerHandler(GET, "/{index}/_search_format", this);
        controller.registerHandler(POST, "/{index}/_search_format", this);
        controller.registerHandler(GET, "/{index}/{type}/_search_format", this);
        controller.registerHandler(POST, "/{index}/{type}/_search_format", this);
        controller.registerHandler(GET, "/_search_format", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        SearchRequest searchRequest = RestSearchAction.parseSearchRequest(request);
        searchRequest.listenerThreaded(false);

        final Charset charset = Charset.forName(
            request.param("charset", "UTF-8")
        );

        client.search(
            searchRequest,
            new FormatListener(
                channel,
                request.param("format", "csv"),
                request.paramAsStringArray("keys", new String[0]),
                request.param("separator", String.valueOf(CSVWriter.DEFAULT_SEPARATOR)).charAt(0),
                request.param("quoteChar", String.valueOf(CSVWriter.DEFAULT_QUOTE_CHARACTER)).charAt(0),
                request.param("escapeChar", String.valueOf(CSVWriter.DEFAULT_ESCAPE_CHARACTER)).charAt(0),
                request.param("lineEnd", CSVWriter.DEFAULT_LINE_END),
                request.param("multiValuedSeparator", " | "),
                request.param("multiValuedQuoteChar", "\"").charAt(0),
                charset
            )
        );
    }
}
