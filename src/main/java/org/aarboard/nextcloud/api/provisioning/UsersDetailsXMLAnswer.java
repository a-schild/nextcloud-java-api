package org.aarboard.nextcloud.api.provisioning;

import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ocs")
public class UsersDetailsXMLAnswer extends XMLAnswer {
	private Data data;

	public List<User> getUsersDetails() {
		List<User> result = new ArrayList<>();
		try {
			Unmarshaller unmarshaller = JAXBContext.newInstance(User.class).createUnmarshaller();
			for (Element user : data.users) {
				DOMSource domSource = new DOMSource(user);
				JAXBElement<User> userElement = unmarshaller.unmarshal(domSource, User.class);
				result.add(userElement.getValue());
			}
		} catch (JAXBException e) {
			throw new NextcloudApiException(e);
		}

		return result;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static final class Data {
		@XmlElementWrapper(name = "users")
		@XmlAnyElement
		private List<Element> users;
	}
}
