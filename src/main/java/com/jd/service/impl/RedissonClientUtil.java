
 
    /**
     * 异步删除指定 ZSet 中的指定 memberName 元素
     */
    public void removeZSetMemberAsync(String key, String memberName) {
        RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(key);
        if (!scoredSortedSet.isExists()) {
            return;
        }
        scoredSortedSet.removeAsync(memberName);
    }
 
 
    /**
     * 异步批量删除指定 ZSet 中的指定 member 元素列表
     */
    public void removeZSetMemberAsync(String key, List<String> memberList) {
        RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(key);
        if (!scoredSortedSet.isExists()) {
            return;
        }
        RBatch batch = client.createBatch();
        memberList.forEach(member -> batch.getScoredSortedSet(key).removeAsync(member));
        batch.execute();
    }



    public boolean isSuperColumn(String javaField)
    {
        return isSuperColumn(this.tplCategory, javaField);
    }

    public static boolean isSuperColumn(String tplCategory, String javaField)
    {
        if (isTree(tplCategory))
        {
            return StringUtils.equalsAnyIgnoreCase(javaField,
                    ArrayUtils.addAll(GenConstants.TREE_ENTITY, GenConstants.BASE_ENTITY));
        }
        return StringUtils.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY);
    }



 /**
     * 返回ZSet分数范围内 member 列表. 区间包含分数本身.
     * 注意这里不能用 -1 代替最大值
     */
    public Collection<Object> getZSetMembersByScoresInclusive(String key, double startScore, double endScore) {
        RScoredSortedSet<Object> scoredSortedSet = client.getScoredSortedSet(key);
        if (!scoredSortedSet.isExists()) {
            return null;
        }
        return scoredSortedSet.valueRange(startScore, true, endScore, true);
    }


public static List<HttpRange> parseRanges(@Nullable String ranges) {
		if (!StringUtils.hasLength(ranges)) {
			return Collections.emptyList();
		}
		if (!ranges.startsWith(BYTE_RANGE_PREFIX)) {
			throw new IllegalArgumentException("Range '" + ranges + "' does not start with 'bytes='");
		}
		ranges = ranges.substring(BYTE_RANGE_PREFIX.length());

		String[] tokens = StringUtils.tokenizeToStringArray(ranges, ",");
		if (tokens.length > MAX_RANGES) {
			throw new IllegalArgumentException("Too many ranges: " + tokens.length);
		}
		List<HttpRange> result = new ArrayList<>(tokens.length);
		for (String token : tokens) {
			result.add(parseRange(token));
		}
		return result;
	}

	private static HttpRange parseRange(String range) {
		Assert.hasLength(range, "Range String must not be empty");
		int dashIdx = range.indexOf('-');
		if (dashIdx > 0) {
			long firstPos = Long.parseLong(range.substring(0, dashIdx));
			if (dashIdx < range.length() - 1) {
				Long lastPos = Long.parseLong(range.substring(dashIdx + 1));
				return new ByteRange(firstPos, lastPos);
			}
			else {
				return new ByteRange(firstPos, null);
			}
		}
		else if (dashIdx == 0) {
			long suffixLength = Long.parseLong(range.substring(1));
			return new SuffixByteRange(suffixLength);
		}
		else {
			throw new IllegalArgumentException("Range '" + range + "' does not contain \"-\"");
		}
	}


public String getMessage() {
		StringBuilder sb = new StringBuilder("Validation failed for argument [")
				.append(this.parameter.getParameterIndex()).append("] in ")
				.append(this.parameter.getExecutable().toGenericString());
		BindingResult bindingResult = getBindingResult();
		if (bindingResult.getErrorCount() > 1) {
			sb.append(" with ").append(bindingResult.getErrorCount()).append(" errors");
		}
		sb.append(": ");
		for (ObjectError error : bindingResult.getAllErrors()) {
			sb.append('[').append(error).append("] ");
		}
		return sb.toString();
	}





	public static List<ResourceRegion> toResourceRegions(List<HttpRange> ranges, Resource resource) {
		if (CollectionUtils.isEmpty(ranges)) {
			return Collections.emptyList();
		}
		List<ResourceRegion> regions = new ArrayList<>(ranges.size());
		for (HttpRange range : ranges) {
			regions.add(range.toResourceRegion(resource));
		}
		if (ranges.size() > 1) {
			long length = getLengthFor(resource);
			long total = 0;
			for (ResourceRegion region : regions) {
				total += region.getCount();
			}
			if (total >= length) {
				throw new IllegalArgumentException("The sum of all ranges (" + total +
						") should be less than the resource length (" + length + ")");
			}
		}
		return regions;
	}


