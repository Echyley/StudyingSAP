package de.hybris.platform.outboundservices.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * This builder is used to mock a body of a batch response body. A batch response body can be composed of multiple batch parts which
 * can be composed of multiple single responses and changesets. A changeset may consist of multiple responses. To mock a batch response
 * body easily and flexibly {@link BatchResponseBodyBuilder} is introduced.
 */
public class BatchResponseBodyBuilder
{
	private final List<BatchPartResponseBuilder> batchPartResponseBuilders = new ArrayList<>();
	private String batchBoundary = "";

	public static BatchResponseBodyBuilder batchResponseBuilder()
	{
		return new BatchResponseBodyBuilder();
	}

	public BatchResponseBodyBuilder withBoundary(final String boundary)
	{
		this.batchBoundary = boundary;
		return this;
	}

	public BatchResponseBodyBuilder withBatchPart(final BatchPartResponseBuilder batchPartResponseBuilder)
	{
		this.batchPartResponseBuilders.add(batchPartResponseBuilder);
		return this;
	}

	/**
	 * This method means to simplify the creation of the responseBody from
	 * SingleResponseBuilder -> ChangeSetBuilder -> BatchPartResponseBuilder -> BatchResponseBuilder
	 * to
	 * SingleResponseBuilder -> (Wrapped in one ChangeSet, wrapped in one BatchPart) -> BatchResponse
	 */
	public BatchResponseBodyBuilder withSingleResponse(final SingleResponseBuilder singleResponseBuilder)
	{
		this.batchPartResponseBuilders.add(
				new BatchPartResponseBuilder().withChangeSet(
						new ChangeSetBuilder().withSingleResponse(singleResponseBuilder)));
		return this;
	}

	public String build()
	{
		if (StringUtils.isBlank(this.batchBoundary) || batchPartResponseBuilders.isEmpty())
		{
			throw new RuntimeException();
		}
		final String batchEnd = "--".concat(this.batchBoundary).concat("--");
		return batchPartResponseBuilders.stream()
		                                .map(builder -> builder.withBoundary(batchBoundary))
		                                .map(BatchPartResponseBuilder::build)
		                                .collect(Collectors.joining()) +
				batchEnd;
	}

	public static BatchPartResponseBuilder batchPartResponseBuilder()
	{
		return new BatchPartResponseBuilder();
	}

	public static class BatchPartResponseBuilder
	{
		private ChangeSetBuilder changeSetBuilder;
		private String boundary = "";

		public BatchPartResponseBuilder withBoundary(final String boundary)
		{
			this.boundary = boundary;
			return this;
		}

		public BatchPartResponseBuilder withChangeSet(final ChangeSetBuilder changeSetBuilder)
		{
			this.changeSetBuilder = changeSetBuilder;
			return this;
		}

		private String generateChangeSetBoundary()
		{
			return "changeset-".concat(UUID.randomUUID().toString());
		}

		public String build()
		{
			if (StringUtils.isBlank(this.boundary))
			{
				this.boundary = "batch";
			}
			if (changeSetBuilder == null)
			{
				this.changeSetBuilder = changeSetBuilder()
				                                        .withSingleResponse(singleResponseBuilder()
				                                                                                 .withStatusCode(201));
			}
			final String changesetBoundary = generateChangeSetBoundary();
			final String batchPartBoundaryStart = "--".concat(this.boundary).concat("\r\n");
			final String batchPartHeader = String.format("Content-Type: multipart/mixed; boundary=%s\r\n\r\n", changesetBoundary);
			return batchPartBoundaryStart + batchPartHeader +
					this.changeSetBuilder.withBoundary(changesetBoundary).build();
		}

	}


	public static ChangeSetBuilder changeSetBuilder()
	{
		return new ChangeSetBuilder();
	}

	public static class ChangeSetBuilder
	{
		private final List<SingleResponseBuilder> responseBuilders = new ArrayList<>();
		private String boundary = "";


		public ChangeSetBuilder withBoundary(final String boundary)
		{
			this.boundary = boundary;
			return this;
		}

		public ChangeSetBuilder withSingleResponse(final SingleResponseBuilder singleResponseBuilder)
		{
			this.responseBuilders.add(singleResponseBuilder);
			return this;
		}

		public String build()
		{
			if (StringUtils.isBlank(this.boundary) || responseBuilders.isEmpty())
			{
				throw new RuntimeException();
			}
			final String boundaryStart = "--".concat(this.boundary).concat("\r\n");
			final String boundaryEnd = String.format("--%s--\r\n", this.boundary);
			return boundaryStart.concat(
					                    StringUtils.join(responseBuilders.stream().map(SingleResponseBuilder::build).toList(), boundaryStart))
			                    .concat(boundaryEnd);
		}
	}


	public static SingleResponseBuilder singleResponseBuilder()
	{
		return new SingleResponseBuilder();
	}

	public static SingleResponseBuilder singleResponseBuilder(final int statusCode)
	{
		return new SingleResponseBuilder().withStatusCode(statusCode);
	}

	public static SingleResponseBuilder singleResponseBuilder(final int statusCode, final String body)
	{
		return new SingleResponseBuilder().withStatusCode(statusCode).withBody(body);
	}

	public static SingleResponseBuilder singleResponseBuilder(final int statusCode, final String contentId, final String body)
	{
		return new SingleResponseBuilder().withStatusCode(statusCode).withBody(body).withContentId(contentId);
	}

	public static class SingleResponseBuilder
	{
		private static final String TEMPLATE = """
				Content-Type: application/http\r
				Content-Transfer-Encoding: binary\r
				Content-Id: {contentId}\r
				\r
				HTTP/1.1 {statusCode} {statusTitle}\r
				Content-Length: {Length}\r
				\r
				{body}\r""";
		private Integer statusCode;
		private String body = "{\"d\":{\"__metadata\":\"notMatters\"}}";
		private String contentId = "testId123";

		public SingleResponseBuilder withStatusCode(final int statusCode)
		{
			this.statusCode = statusCode;
			return this;
		}

		public SingleResponseBuilder withBody(final String body)
		{
			this.body = body;
			return this;
		}

		public SingleResponseBuilder withContentId(final String contentId)
		{
			this.contentId = contentId;
			return this;
		}

		public String build()
		{
			if (this.statusCode == null)
			{
				throw new RuntimeException();
			}
			return TEMPLATE.replace("{statusCode}", this.statusCode.toString())
			               .replace("{statusTitle}", HttpStatus.valueOf(statusCode).getReasonPhrase())
			               .replace("{Length}", String.valueOf(this.body.length()))
			               .replace("{contentId}", String.valueOf(this.contentId))
			               .replace("{body}", this.body);
		}
	}
}
