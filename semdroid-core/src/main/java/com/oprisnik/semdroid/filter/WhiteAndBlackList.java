/*
 * Copyright 2014 Alexander Oprisnik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oprisnik.semdroid.filter;

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.StringList;

/**
 * Combined white- and blacklist.
 */
public class WhiteAndBlackList implements StringFilter {

    public static final String KEY_WHITELIST = "whitelist";
    public static final String KEY_BLACKLIST = "blacklist";

    private StringList mWhitelist;
    private StringList mBlacklist;
    private boolean mUseWhitelist = false;
    private boolean mUseBlacklist = false;

    public WhiteAndBlackList() {
    }

    @Override
    public void init(Config config) throws BadConfigException {
        if (config == null) {
            return;
        }
        initBlacklist(config.getSubconfig(KEY_BLACKLIST));
        initWhitelist(config.getSubconfig(KEY_WHITELIST));
    }

    @Override
    public boolean use(String data) {
        return (isOnWhitelist(data) && !isOnBlacklist(data));
    }

    @Override
    public boolean checkPrefix(String data) {
        return (isOnWhitelistCheckPrefix(data) && !isOnBlacklistCheckPrefix(data));
    }

    public void init(StringList whitelist, StringList blacklist) {
        setWhitelist(whitelist);
        setBlacklist(blacklist);
    }

    public void initBlacklist(Config blacklistConfig) throws BadConfigException {
        if (blacklistConfig == null) {
            mUseBlacklist = false;
            return;
        }
        mUseBlacklist = true;

        StringList list = new StringList();
        list.init(blacklistConfig);
        setBlacklist(list);
    }

    public void initWhitelist(Config whitelistConfig) throws BadConfigException {
        if (whitelistConfig == null) {
            mUseWhitelist = false;
            return;
        }
        mUseWhitelist = true;

        StringList list = new StringList();
        list.init(whitelistConfig);
        setWhitelist(list);
    }

    public StringList getBlacklist() {
        return mBlacklist;
    }

    public void setBlacklist(StringList blacklist) {
        mBlacklist = blacklist;
    }

    public StringList getWhitelist() {
        return mWhitelist;
    }

    public void setWhitelist(StringList whitelist) {
        mWhitelist = whitelist;
    }

    public boolean isOnBlacklist(String string) {
        if (!mUseBlacklist)
            return false;
        return mBlacklist.contains(string);
    }

    public boolean isOnBlacklistCheckPrefix(String string) {
        if (!mUseBlacklist)
            return false;
        return mBlacklist.checkPrefix(string);
    }

    public boolean isOnWhitelist(String string) {
        if (!mUseWhitelist)
            return true;
        return mWhitelist.contains(string);
    }

    public boolean isOnWhitelistCheckPrefix(String string) {
        if (!mUseWhitelist)
            return true;
        return mWhitelist.checkPrefix(string);
    }

    public boolean usesWhitelist() {
        return mUseWhitelist;
    }

    public void useWhitelist(boolean useWhitelist) {
        mUseWhitelist = useWhitelist;
    }

    public boolean usesBlacklist() {
        return mUseBlacklist;
    }

    public void useBlacklist(boolean useBlacklist) {
        mUseBlacklist = useBlacklist;
    }

}
