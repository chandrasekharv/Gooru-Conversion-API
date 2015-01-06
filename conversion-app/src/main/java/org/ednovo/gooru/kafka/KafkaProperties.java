package org.ednovo.gooru.kafka;


public final class KafkaProperties {

	public static final String ZK_CONNECT = "metadata.broker.list";
	public static final String ZK_CONSUMER_CONNECT = "zookeeper.connect";
	public static final String ZK_CONSUMER_GROUP = "group.id";
	public static final String GROUP_ID = "groupid";
	public static final String TOPIC = "topic";
	public static final String KAFKA_SERVER_URL = "kafkaServerURL";
	public static final String KAFKA_SERVER_PORT = "kafkaServerPort";
	public static final int KAFKA_SERVER_PORT_VALUE = 9092;
	public static final String KAFKA_PRODUCER_BUFFER_SIZE = "kafkaProducerBufferSize";
	public static final int KAFKA_PRODUCER_BUFFER_SIZE_VALUE = 64 * 1024;
	public static final String CONNECTION_TIME_OUT = "connectionTimeOut";
	public static final int CONNECTION_TIME_OUT_VALUE = 100000;
	public static final String RECONNECT_INTERVAL = "reconnectInterval";
	public static final int RECONNECT_INTERVAL_VALUE = 10000;
	public static final String SERIALIZER_CLASS = "serializer.class";
	public static final String SERIALIZER_CLASS_VALUE = "kafka.serializer.StringEncoder";
	public static final String PRODUCER_TYPE = "producer.type";
	public static final String PRODUCER_TYPE_VALUE = "async";
	public static final String COMPRESSION_CODEC = "compression.codec";
	public static final String COMPRESSION_CODEC_VALUE = "1";
	public static final String ZK_SESSION_TIME_OUT_MS = "zookeeper.session.timeout.ms";
	public static final String ZK_SESSION_TIME_OUT_MS_VALUE = "10000";
	public static final String ZK_SYNCTIME_MS = "zookeeper.sync.time.ms";
	public static final String ZK_SYNCTIME_MS_VALUE = "200";
	public static final String AUTOCOMMIT_INTERVAL_MS = "auto.commit.interval.ms";
	public static final String AUTOCOMMIT_INTERVAL_MS_VALUE = "1000";
	public static final String FETCH_SIZE = "fetch.size";
	public static final String FETCH_SIZE_VALUE = "1048576";
	public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
	public static final String AUTO_OFFSET_RESET_VALUE = "smallest";
	public static final String KAFKA_PREFIX = "kafka.";
	public static final String REQUEST_REQUIRED_ACKS = "request.required.acks";
	public static final String REQUEST_REQUIRED_ACKS_VALUE = "1";
	public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
	public static final String RETRY_BACKOFF_MS_VALUE = "1000";
	public String zkConnectValue;
	public String groupIdValue;
	public String zkConsumerConnectValue;
	public String consumerGroupIdValue;
	public String topicValue;
	public String kafaServiceUrl;
	public String kafkaIp;
	public String kafkaConversionTopic;
	public static final String KAFKA_PORT = "9092";

	public String getKafkaConversionTopic() {
		return kafkaConversionTopic;
	}

	public void setKafkaConversionTopic(String kafkaConversionTopic) {
		this.kafkaConversionTopic = kafkaConversionTopic;
	}

	public String getKafkaIp() {
		return kafkaIp;
	}

	public void setKafkaIp(String kafkaIp) {
		this.kafkaIp = kafkaIp;
	}

	public String getZkConnectValue() {
		return buildEndPoint(getKafkaIp(), KAFKA_PORT);
	}

	public static String buildEndPoint(String ip, String portNo) {

		StringBuffer stringBuffer = new StringBuffer();
		String[] ips = ip.split(",");
		String[] ports = portNo.split(",");
		for (int count = 0; count < ips.length; count++) {

			if (stringBuffer.length() > 0) {
				stringBuffer.append(",");
			}

			if (count < ports.length) {
				stringBuffer.append(ips[count] + ":" + ports[count]);
			} else {
				stringBuffer.append(ips[count] + ":" + ports[0]);
			}
		}
		return stringBuffer.toString();
	}
}
