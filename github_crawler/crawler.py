# TODO: problem  API is limited to 1000 repos per search query (https://developer.github.com/v3/search/#about-the-search-api)

import pdb
import requests
import json
import numpy as np

fetching_rate=100
url='https://api.github.com/graphql'
token=""
headers={'Authorization': 'token '+token}
repo_search_query="language:Java stars:>1350"
repo_search_graphql="""
{{
  search(query: \"{0}\", type: REPOSITORY, first: {1}{2})  {{
    pageInfo {{
      hasNextPage
      startCursor
      endCursor
    }}
    repositoryCount
    edges {{
      node {{
        ... on Repository {{
          name
          createdAt
          updatedAt
          diskUsage
          stargazers {{
           totalCount
          }}
          forks {{
           totalCount
          }}
          watchers {{
           totalCount
          }}
        }}
      }}
    }}
  }}
}}
"""

# methods to extract data from result
def next_page(response):
    try:
        return response["data"]["search"]["pageInfo"]["hasNextPage"]
    except Exception:
        print_error(response)
        raise
def get_next_page_id(response):
    try:
        return response["data"]["search"]["pageInfo"]["endCursor"]
    except Exception:
        print_error(response)
        raise
def get_total_repos(response):
    try:
        return response["data"]["search"]["repositoryCount"]
    except Exception:
        print_error(response)
        raise
def get_edges(response):
    try:
        return response["data"]["search"]["edges"]
    except Exception:
        print_error(response)
        raise

# request data from github related methods.
def build_repo_search_request(query, first, after):
    modified_after=""
    if after!="":
        modified_after= ", after: \""+ after + "\""
    return repo_search_graphql.format(query, first, modified_after)
def do_request(query):
    r2=requests.post(url, json.dumps({"query": query}), headers=headers)
    r=r2.json()
    ajson=json.loads(json.dumps(r))
    return ajson;
def extract_repo(response):
    number_extacted=0
    np_numeric_data=np.zeros(shape=(len(get_edges(response)),4))
    repo_names=[]
    for edges in get_edges(response):
        repo=edges['node']
        total_stars=repo['stargazers']['totalCount']
        total_watched=repo['watchers']['totalCount']
        total_forked=repo['forks']['totalCount']
        total_size=repo['diskUsage']
        name=repo['name']
        repo_names.append(name)
        np_numeric_data[number_extacted, :] = [total_stars, total_forked, total_size, total_watched]
        number_extacted+=1
    return number_extacted, repo_names, np_numeric_data

# utility methods.
def calculate_significance(a):
    return a[0]+(.75*(1-a[2]))+(.5*(a[3]+a[1]))
def print_error(response):
    print "---->ERROR:"
    print response
    print "---->Exception:"

# main loop
current_page_id=""
repo_dict = []
np_repo_data=np.zeros(shape=(0, 4))
while True:
    request_str=build_repo_search_request(repo_search_query, fetching_rate, current_page_id)
    response=do_request(request_str)
    current_page_id=get_next_page_id(response)
    number_extracted, temp_repo_names, temp_repo_data=extract_repo(response)
    np_repo_data=np.concatenate((np_repo_data, temp_repo_data), axis=0)
    repo_dict=repo_dict+temp_repo_names
    print "fetched {0}. Progress: {1} out of {2}." \
        .format(number_extracted, len(repo_dict), get_total_repos(response))
    if not next_page(response):
        break

print "-------"

#normalize and calculate significance.
normalized_repo_data=np_repo_data/np_repo_data.max(axis=0)
significance=np.apply_along_axis(calculate_significance, 1, normalized_repo_data)

#create dictionary (there is more platonic ways of doing this but anyways...)
repo_dict2={}
index=0
for repo in  repo_dict:
    repo_dict2[repo]=significance[index]
    index+=1

#order by decreasing significance and print
repo_dict_sorted=sorted(repo_dict2.items(), key=lambda x : x[1], reverse=True)
for repo in repo_dict_sorted:
    print repo[0]
