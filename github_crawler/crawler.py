import requests
import json
from pprint import pprint

url='https://api.github.com/graphql'
token=""
headers={'Authorization': 'token '+token}
repo_search_query="language:Java stars:>6000"
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

def build_repo_search_request(query, first, after):
    modified_after=""
    if after!="":
        modified_after= ", after: \""+ after + "\""
    return repo_search_graphql.format(query, first, modified_after)
def do_request(query):
    r2=requests.post(url, json.dumps({"query": query}), headers=headers)
    r=r2.json()
    ajson=json.loads(json.dumps(r))
#   pprint(json.dumps(r))
    return ajson;
def next_page(response):
    return response["data"]["search"]["pageInfo"]["hasNextPage"]
def get_next_page_id(response):
    return response["data"]["search"]["pageInfo"]["endCursor"]
def get_total_repos(repose):
    return response["data"]["search"]["repositoryCount"]
def extract_repo(repo_dict, response):
    number_extacted=0
    for edges in response["data"]["search"]["edges"]:
        number_extacted+=1
        repo=edges['node']
        total_stars=repo['stargazers']['totalCount']
        total_watched=repo['watchers']['totalCount']
        total_forked=repo['forks']['totalCount']
        total_size=repo['diskUsage']
        name=repo['name']
        significance=total_stars+(.75*(1-total_size))+(.5*(total_watched+total_forked))
        repo_dict.append({"significance" : significance,  "stars" : total_stars, \
                "watched" : total_watched, \
                "forked" : total_forked, \
                "size" : total_size,
                "name" : name})
    return number_extacted

current_page_id=""
repo_dict = []
while True:
    request_str=build_repo_search_request(repo_search_query, 100, current_page_id)
    response=do_request(request_str)
    current_page_id=get_next_page_id(response)
    number_extracted=extract_repo(repo_dict, response)
    print "fetched {0}. Progress: {1} out of {2}." \
        .format(number_extracted, len(repo_dict), get_total_repos(response))
    if not next_page(response):
        break

newlist=sorted(repo_dict, key=lambda k: k['significance'], reverse=True)
print("total number of projects extracted: {0}".format(len(repo_dict)))
for repo_ex in newlist:
    print repo_ex["name"],
    print ",",
    print repo_ex["significance"],
    print ",",
    print repo_ex["stars"],
    print ",",
    print repo_ex["forked"],
    print ",",
    print repo_ex["watched"],
    print ",",
    print repo_ex["size"]
