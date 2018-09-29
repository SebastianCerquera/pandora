package pandora.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import pandora.client.model.RSAProblem;

@SpringBootTest
public class RSAProblemTest {

	@Test
	public void jsonParsing() {
		RSAProblem problem = new RSAProblem(
				"{\\\"id\\\":4,\\\"modulus\\\":\\\"822459998706546549516547370371\\\",\\\"secret\\\":\\\"905208516560207\\\",\\\"delay\\\":\\\"300\\\",\\\"state\\\":\\\"COMPLETED\\\",\\\"images\\\":[{\\\"id\\\":5,\\\"name\\\":\\\"1.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":6,\\\"name\\\":\\\"10.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":7,\\\"name\\\":\\\"2.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":8,\\\"name\\\":\\\"3.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":9,\\\"name\\\":\\\"4.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":10,\\\"name\\\":\\\"5.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":11,\\\"name\\\":\\\"6.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":12,\\\"name\\\":\\\"7.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":13,\\\"name\\\":\\\"8.jpg\\\",\\\"blob\\\":\\\"\\\"},{\\\"id\\\":14,\\\"name\\\":\\\"9.jpg\\\",\\\"blob\\\":\\\"\\\"}]}");

		assertThat(problem.getDelay()).isEqualTo("300");
		assertThat(problem.getModulus()).isEqualTo("822459998706546549516547370371");
		assertThat(problem.getSecret()).isEqualTo("905208516560207");
	}

}
