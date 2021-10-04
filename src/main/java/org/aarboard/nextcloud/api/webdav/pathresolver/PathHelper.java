/*
 * Copyright (C) 2021 aschi
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
package org.aarboard.nextcloud.api.webdav.pathresolver;

/**
 *
 * @author aschi
 */
public class PathHelper {
    /**
     * Concat path elements and make sure we have everything enclosed
     * with on / , including one at the start and (optional) end
     * 
     * 
     * @param endWithSlash
     * @param p1
     * @param elements
     * @return 
     */
    public static String concatPathElements(
            boolean endWithSlash,
            String p1, 
            String ... elements)
    {
        StringBuilder sb= new StringBuilder();
        if (!p1.startsWith("/"))
        {
            sb.append('/');
        }
        sb.append(p1);
        
        if (elements != null)
        {
            for (String rp : elements)
            {
                if (rp != null && !rp.isEmpty())
                {
                    if (sb.length()>0 )
                    {
                        if (sb.charAt(sb.length()-1) == '/')
                        {
                            // Previous element ends with /
                            if (rp.charAt(0) == '/')
                            {
                                // Ok, add everything, without the first char
                                sb.append(rp.substring(1));
                            }
                            else
                            {
                                // Ok, add everything
                                sb.append(rp);
                            }
                        }
                        else
                        {
                            if (sb.charAt(sb.length()-1) != '/' &&
                                rp.charAt(0) != '/')
                            {
                                sb.append('/');
                            }
                            sb.append(rp);
                        }
                    }
                    else
                    {
                        if (rp.charAt(0) == '/')
                        {
                            // Ok, add everything
                            sb.append(rp);
                        }
                        else
                        {
                            // Ok, add everything
                            sb.append('/');
                            sb.append(rp);
                        }
                    }
                }
            }
        }
        // Final / if needed
        if (endWithSlash && sb.charAt(sb.length()-1) != '/')
        {
            sb.append('/');
        }
        if (!endWithSlash && sb.charAt(sb.length()-1) == '/')
        {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
    
}
