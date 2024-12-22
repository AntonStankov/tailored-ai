terraform {
  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}
variable "do_token" {}
variable "ssh_key" {}
variable "ssh_interconnection" {}



provider "digitalocean" {
  token = var.do_token
}

variable "ssh_key_path" {
  description = "/home/antonstankov/.ssh/id_rsa.pub"
  default     = "/home/antonstankov/.ssh/id_rsa.pub"
}

variable "droplet_name" {
  description = "Name for the droplet"
  default     = "example-droplet"
}

variable "region" {
  description = "DigitalOcean region"
  default     = "nyc3"
}

variable "size" {
  description = "Droplet size"
  default     = "s-1vcpu-2gb"
}

resource "digitalocean_ssh_key" "ssh_key" {
  name       = "terraform"
  public_key = var.ssh_key
}

resource "digitalocean_ssh_key" "ssh_inter" {
  name       = "terraform"
  public_key = var.ssh_interconnection
}

resource "digitalocean_firewall" "firewall" {
  name = "snaptalk-firewall"

  droplet_ids = [
    digitalocean_droplet.snaptalk.id,
    digitalocean_droplet.tst-machine.id
  ]

  inbound_rule {
    protocol           = "tcp"
    port_range         = "1-65535"
    source_addresses   = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol           = "tcp"
    port_range         = "3306"
    source_addresses   = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "3306"
    destination_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol           = "tcp"
    port_range         = "22"
    source_addresses   = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "1-65535"
    destination_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol           = "tcp"
    port_range         = "5000"
    source_addresses   = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "5000"
    destination_addresses = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "22"
    destination_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol           = "tcp"
    port_range         = "80"
    source_addresses   = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "80"
    destination_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol           = "tcp"
    port_range         = "443"
    source_addresses   = ["0.0.0.0/0"]
  }
  outbound_rule {
    protocol           = "tcp"
    port_range         = "443"
    destination_addresses = ["0.0.0.0/0"]
  }
}

resource "digitalocean_droplet" "snaptalk" {
  name   = "snaptalk"
  region = var.region
  size   = var.size
  image  = "ubuntu-20-04-x64"

  ssh_keys    = [digitalocean_ssh_key.ssh_key.fingerprint]
  monitoring  = true
  ipv6        = true
}

resource "digitalocean_droplet" "tst-machine" {
  name   = "tst-machine"
  region = var.region
  size   = var.size
  image  = "ubuntu-20-04-x64"

  ssh_keys    = [digitalocean_ssh_key.ssh_key.fingerprint, digitalocean_ssh_key.ssh_inter.fingerprint]
  monitoring  = true
  ipv6        = true
}