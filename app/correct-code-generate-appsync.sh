cd build/generated/source/appsync && LC_ALL=C && LANG=C && find . -type f | xargs sed -i '' 's/@Nullable @Nullable/@Nullable/g' && find . -type f | xargs sed -i '' 's/@Nonnull @Nonnull/@Nonnull/g'
