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
package org.aarboard.nextcloud.api.utils;

import java.io.Reader;

/**
 * Response parser for endpoints that return no meaningful body (e.g. some of
 * the group folders endpoints). The response is ignored and {@code null} is
 * returned.
 */
public class EmptyAnswerParser implements ConnectorCommon.ResponseParser<Void> {

    private static volatile EmptyAnswerParser instance;

    public static EmptyAnswerParser getInstance() {
        if (instance == null) {
            synchronized (EmptyAnswerParser.class) {
                if (instance == null) {
                    instance = new EmptyAnswerParser();
                }
            }
        }
        return instance;
    }

    @Override
    public Void parseResponse(Reader reader) {
        return null;
    }
}
