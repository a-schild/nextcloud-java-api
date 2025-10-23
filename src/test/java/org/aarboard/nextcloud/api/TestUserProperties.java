/*
 * Copyright (C) 2017 a.schild
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aarboard.nextcloud.api;

import org.aarboard.nextcloud.api.provisioning.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * Tests for the User API.
 *
 * @author Stefan Endrullis
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUserProperties extends ATestClass {

	@Test
	public void t09_01_testGetCurrentUser() {
		System.out.println("getCurrentUser");
		if (_nc != null) {
			User user = _nc.getCurrentUser();
			assertNotNull(user);
			assertTrue(StringUtils.isNotBlank(user.getId()));
			assertTrue(StringUtils.isNotBlank(user.getBackend()));
            assertTrue(user.getLastLogin() != 0);
		}
	}

}
