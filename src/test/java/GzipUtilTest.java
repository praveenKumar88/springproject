import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.ReplyRequiredException;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.ArrayList;

public class GzipUtilTest {

    private AbstractApplicationContext context;

    private DirectChannel inputChannel;

    private DirectChannel outputChannel;

    private static final String TEST_INPUT_CHANNEL = "inputChannel";

    private static final String TEST_OUTPUT_CHANNEL = "outputChannel";

    private ArrayList<String> configLocations = new ArrayList<>();

    @Before
    public void setUp() {

        configLocations.add("classpath:decompress.xml");
        String[] arr = new String[configLocations.size()];
        context = new ClassPathXmlApplicationContext(configLocations.toArray(arr));
        inputChannel = (DirectChannel) context.getBean(TEST_INPUT_CHANNEL);
        outputChannel = (DirectChannel)context.getBean(TEST_OUTPUT_CHANNEL);
    }

    @After
    public void tearDown() {
        this.context.close();
    }

    /**
     * The exception thrown is due to spring integration zip unable to
     * handle gzip. See:
     * https://stackoverflow.com/questions/53017253/spring-integration-unzip-transformer-throws-replyrequiredexception-exception
     * @throws Exception
     */
    @Test
    public void testSpringIntegrationZipThrowsException() throws Exception {
        String xml = "<Hello>World</Hello>";
        byte[] actual = GzipUtil.zip(xml);
        Message message = new GenericMessage<>(actual);
        try {
            inputChannel.send(message);
        } catch (ReplyRequiredException exception) {
            exception.printStackTrace();
        }
    }
}