
 
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
