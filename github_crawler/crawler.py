#!/usr/bin/env python2

# TODO: problem  API is limited to 1000 repos per search query (https://developer.github.com/v3/search/#about-the-search-api)

import sys
import requests
import json
import numpy as np

class repo_discriptor:
    def __init__ (self, url, size, star_count, watched_count, fork_count,  \
                 significance,  \
                 normalized_size, normalized_star_count, normalized_watched_count, normalized_fork_count):
        self.size=size
        self.star_count=star_count
        self.watched_count=watched_count
        self.fork_count=fork_count

        self.normalized_size=normalized_size
        self.normalized_star_count=normalized_star_count
        self.normalized_watched_count=normalized_watched_count
        self.normalized_fork_count=normalized_fork_count

        self.significance=significance
        self.url=url
        #self.name=url
        #self.user="?"

    def __str_dump (self) :
        s = "Repo: %s\n  name '%s'\n  significance %.3f\n  stars %.3f/%d\n  size %.3f/%d\n  watchers %.3f/%d\n  forks %.3f/%d\n" % \
                (self.url,
                "?",
                self.significance,
                self.normalized_star_count,    self.star_count,
                self.normalized_size,          self.size,
                self.normalized_watched_count, self.watched_count,
                self.normalized_fork_count,    self.fork_count)
        return s

    def __str__ (self) :
        return self.__str_dump ()

fetching_rate=100
url='https://api.github.com/graphql'
token="" # get one here: https://github.com/settings/tokens
headers={'Authorization': 'token '+token}
repo_search_query="language:Java stars:>4500"
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
    # a[0] stars
    # a[1] forks
    # a[2] size
    # a[3] watch
    return a[0]+(.25*(1-a[2]))+(.5*(a[3]+a[1]))

def print_error(response):
    print "---->ERROR:"
    print response
    print "---->Exception:"

def print_repo_discriptor_list(repo_discriptor_list):
    for discriptor in repo_discriptor_list:
        print "{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}".format(discriptor.url, discriptor.significance,  \
                discriptor.size, discriptor.star_count, discriptor.watched_count, discriptor.fork_count, \
                discriptor.normalized_size, discriptor.normalized_star_count, discriptor.normalized_watched_count, discriptor.normalized_fork_count)

# main function, gets list of 'repo_disciptors'
# based on the defined parameters.
# list is ordered by decreasing significance.
def get_repo_discriptors():
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
    #create objects (there may be a more platonic ways of doing this but anyways...)
    index=0
    repo_object_list=[]
    for repo in  repo_dict:
        discriptor=repo_discriptor(repo,
                np_repo_data[index, 2], np_repo_data[index,0], \
                np_repo_data[index, 3], np_repo_data[index, 1],
                significance[index],
                normalized_repo_data[index, 2], normalized_repo_data[index,0], \
                normalized_repo_data[index, 3], normalized_repo_data[index, 1])
        repo_object_list.append(discriptor)
        index+=1
    #order by decreasing significance
    repo_object_list.sort(key=lambda x : x.significance, reverse=True)
    return repo_object_list

def repo_code_has_keyword (repo_name, keyword) :
    q = 'extension:java in:file thread repo:{0}' % repo_name
    post_data = '''
    {{
      search(query: "{0}", type: REPOSITORY, first: {1}{2})  {{
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
    '''
    return False

def has_concurrency (repo) :
    pass

def main () :
    if token == "" :
        print 'Empty token, please provide one'
        return 1

    # demo
    alist=get_repo_discriptors()
    print_repo_discriptor_list(alist)

    for r in alist :
        print r


if __name__ == '__main__' :
    ret = main ()
    sys.exit (ret)
