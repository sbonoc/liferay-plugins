/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.engine.documentlibrary.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.sync.engine.model.SyncFile;
import com.liferay.sync.engine.service.SyncFileService;
import com.liferay.sync.engine.util.FilePathNameUtil;

import java.util.Map;

/**
 * @author Shinn Lok
 */
public class AddFileEntryEvent extends BaseEvent {

	public AddFileEntryEvent(
		long syncAccountId, Map<String, Object> parameters) {

		super(syncAccountId, _URL_PATH, parameters);
	}

	@Override
	protected void processResponse(String response) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		SyncFile syncFile = objectMapper.readValue(
			response, new TypeReference<SyncFile>() {});

		SyncFile parentSyncFile = SyncFileService.fetchSyncFile(
			syncFile.getParentFolderId(), syncFile.getRepositoryId(),
			getSyncAccountId());

		String filePathName = null;

		if (parentSyncFile != null) {
			filePathName = FilePathNameUtil.getFilePathName(
				parentSyncFile.getFilePathName(), syncFile.getName());
		}

		syncFile.setFilePathName(filePathName);

		syncFile.setSyncAccountId(getSyncAccountId());

		SyncFileService.update(syncFile);
	}

	private static final String _URL_PATH =
		"/sync-web.syncdlobject/add-file-entry";

}