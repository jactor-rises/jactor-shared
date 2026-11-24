#!/usr/bin/env bash
set -euo pipefail
set -x

TAG=""

if [[ "${GITHUB_REF_TYPE:-}" == "tag" && -n "${GITHUB_REF_NAME:-}" ]]; then
    TAG="${GITHUB_REF_NAME}"
fi

if [[ -z "${TAG}" ]]; then
    HEAD_TAG=$(git tag --points-at HEAD "v[0-9]*.[0-9]*.[0-9]*" | LC_ALL=C sort -V | tail -n 1)
    if [[ -n "${HEAD_TAG}" ]]; then
        TAG="${HEAD_TAG}"
    fi
fi

if [[ -z "${TAG}" ]]; then
    echo "No valid tag found for release."
    echo "release_ready=false" >> "$GITHUB_OUTPUT"
    exit 0
fi

VERSION="${TAG#v}"
OWNER=${GITHUB_REPOSITORY%%/*}
PACKAGE_NAME="${GROUP_ID}.${ARTIFACT_ID}"
EXISTS=false
PACKAGE_LOOKUP_SUCCEEDED=false
PACKAGE_NAMES=("${PACKAGE_NAME}" "${GROUP_ID}:${ARTIFACT_ID}" "${ARTIFACT_ID}")
PACKAGES_JSON=""

if ! PACKAGES_JSON=$(gh api --paginate --slurp "/orgs/${OWNER}/packages?package_type=maven&per_page=100" 2>/dev/null); then
    PACKAGES_JSON=$(gh api --paginate --slurp "/users/${OWNER}/packages?package_type=maven&per_page=100" 2>/dev/null) || true
fi

if [[ -n "${PACKAGES_JSON}" ]]; then
    while IFS= read -r package_name; do
        [[ -n "${package_name}" ]] && PACKAGE_NAMES+=("${package_name}")
    done < <(
    jq -r \
        --arg coordinate "${PACKAGE_NAME}" \
        --arg artifact "${ARTIFACT_ID}" \
        --arg repository "${GITHUB_REPOSITORY}" \
        'add | .[] | select(.name == $coordinate or .name == ($coordinate | gsub("\\."; ":")) or .name == $artifact or .repository.full_name == $repository) | .name' \
        <<< "${PACKAGES_JSON}"
)
fi

for package_name in "${PACKAGE_NAMES[@]}"; do
    PKG_ENCODED=$(printf '%s' "${package_name}" | jq -sRr @uri)
    VERSIONS_JSON=""

    if ! VERSIONS_JSON=$(gh api --paginate --slurp "/orgs/${OWNER}/packages/maven/${PKG_ENCODED}/versions?per_page=100" 2>/dev/null); then
        VERSIONS_JSON=$(gh api --paginate --slurp "/users/${OWNER}/packages/maven/${PKG_ENCODED}/versions?per_page=100" 2>/dev/null) || continue
    fi

    PACKAGE_LOOKUP_SUCCEEDED=true
    if jq -e --arg version "${VERSION}" 'add | any(.[]; .name == $version)' <<< "${VERSIONS_JSON}" > /dev/null; then
        echo "GitHub Packages version ${VERSION} already exists; skipping publication."
        EXISTS=true
        break
    fi
done

if [[ "${PACKAGE_LOOKUP_SUCCEEDED}" != "true" ]]; then
    echo "::warning::Could not verify whether package version ${VERSION} already exists; attempting publication."
fi

echo "tag=${TAG}" >> "$GITHUB_OUTPUT"
echo "version=${VERSION}" >> "$GITHUB_OUTPUT"
echo "pkg_exists=${EXISTS}" >> "$GITHUB_OUTPUT"
echo "release_needed=true" >> "$GITHUB_OUTPUT"
