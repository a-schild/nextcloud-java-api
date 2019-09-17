package org.aarboard.nextcloud.api.provisioning;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.aarboard.nextcloud.api.utils.XMLAnswer;

@XmlRootElement(name = "ocs")
public class UserXMLAnswer extends XMLAnswer
{
	@XmlElement(name = "data")
	private User user;

	public User getUser()
	{
		return user;
	}
}
