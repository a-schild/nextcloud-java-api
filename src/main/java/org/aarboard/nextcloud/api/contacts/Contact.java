/*
 * Copyright (C) 2026 a.schild
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
package org.aarboard.nextcloud.api.contacts;

import org.aarboard.nextcloud.api.dav.DavEntry;

/**
 * A single resource of an address book, holding one vCard document.
 * <p>
 * {@link #getData()} returns the vCard text verbatim; parsing it is left to the
 * caller, for example with ez-vcard.
 *
 * @author a.schild
 * @since 14.3
 */
public class Contact extends DavEntry {
}
