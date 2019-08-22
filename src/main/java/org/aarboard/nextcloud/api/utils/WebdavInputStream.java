/*
 * Copyright (C) 2019 andre
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

import com.github.sardine.Sardine;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.ProxyInputStream;

/**
 *
 * @author andre
 */
public class WebdavInputStream extends ProxyInputStream {

    private final Sardine sardine; // Sardine instance used
    
    public WebdavInputStream(Sardine sardine, InputStream in)
    {
        super(in);
        this.sardine= sardine;
    }

    @Override
    public void close() throws IOException {
        super.close();
        sardine.shutdown();
    }
    
    
    
}
